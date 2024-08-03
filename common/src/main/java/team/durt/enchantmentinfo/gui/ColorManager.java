package team.durt.enchantmentinfo.gui;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import team.durt.enchantmentinfo.Constants;

import java.io.File;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

public class ColorManager {
    private static ColorManager instance;

    private static final ResourceLocation RED_PROPERTIES = colorPropertiesResourceLocation("red");
    private static final ResourceLocation GREEN_PROPERTIES = colorPropertiesResourceLocation("green");
    private static final ResourceLocation BLUE_PROPERTIES = colorPropertiesResourceLocation("blue");

    public static final int RED_DEFAULT = -1240534; //#ffed122a
    public static final int GREEN_DEFAULT = -5046528; //#ffb2ff00
    public static final int BLUE_DEFAULT = -13273872; //#ff3574f0

    private Integer RED_CUSTOM = null;
    private Integer GREEN_CUSTOM = null;
    private Integer BLUE_CUSTOM = null;

    final String regex = "([0-9a-f]{6}|[0-9a-f]{8})";

    private static boolean rainbow = false;

    private ColorManager() {}

    public static synchronized ColorManager getInstance() {
        if (instance == null) {
            instance = new ColorManager();
        }
        return instance;
    }

    public int getRed() {
        return RED_CUSTOM == null ? RED_DEFAULT : RED_CUSTOM;
    }

    public int getGreen() {
        return GREEN_CUSTOM == null ? GREEN_DEFAULT : GREEN_CUSTOM;
    }

    public int getBlue() {
        if (rainbow) return rainbowColor();
        return BLUE_CUSTOM == null ? BLUE_DEFAULT : BLUE_CUSTOM;
    }

    public void load(ResourceManager resourceManager, ProfilerFiller profiler) {
        profiler.push(Constants.MOD_ID + "_colors");
        // not rgb, just red, green and blue lines
        RED_CUSTOM = assignResource(resourceManager, RED_PROPERTIES);
        GREEN_CUSTOM = assignResource(resourceManager, GREEN_PROPERTIES);
        BLUE_CUSTOM = assignResource(resourceManager, BLUE_PROPERTIES);

        try {
            rainbow = new File("config/" + Constants.MOD_ID + ".rainbow").exists();
        } catch (Exception ignored) {
            rainbow = false;
        }
        profiler.pop();
    }

    private Integer assignResource(ResourceManager resourceManager, ResourceLocation resourceLocation) {
        Integer value = null;
        Optional<Resource> optionalResource = resourceManager.getResource(resourceLocation);
        if (optionalResource.isPresent()) {
            Resource resource = optionalResource.get();
            try {
                Properties properties = new Properties();
                properties.load(resource.open());
                value = pullColorFromProperties(properties);
            } catch (Exception exception) {
                String path = String.format("%s/%s/%s", resource.sourcePackId(), resourceLocation.getNamespace(), resourceLocation.getPath());
                Constants.LOG.error(
                        "Could not correctly get properties from \"" + path + "\"",
                        exception
                );
            }
        }
        return value;
    }

    private Integer pullColorFromProperties(@NotNull Properties properties) throws Exception {
        String colorHex = properties.getProperty("color");
        if (colorHex == null) {
            throw new NullPointerException("Could not get property \"color\"");
        } else if (colorHex.isEmpty()) {
            throw new IllegalArgumentException("Cant pass empty String as color");
        }
        colorHex = colorHex.toLowerCase(Locale.ROOT);
        if (!colorHex.matches(regex)) {
            throw new IllegalArgumentException(colorHex + " does not match " + regex);
        }
        int colorInt = Integer.valueOf(colorHex, 16);
        if (colorHex.length() == 6) {
            return (255 << 24) + colorInt;
        }
        return colorInt;
    }

    private static ResourceLocation colorPropertiesResourceLocation(String colorName) {
        return new ResourceLocation(Constants.MOD_ID, "textures/tooltip/colors/" + colorName + ".properties");
    }

    private static int rainbowColor() {
        int i = (int) (System.currentTimeMillis() % 1536);
        int mode = i / 256;
        int val = i % 256;
        int r = 0;
        int g = 0;
        int b = 0;
        switch (mode) {
            case 0 -> {
                r = 255;
                g = val;
            }
            case 1 -> {
                r = 255 - val;
                g = 255;
            }
            case 2 -> {
                g = 255;
                b = val;
            }
            case 3 -> {
                g = 255 - val;
                b = 255;
            }
            case 4 -> {
                b = 255;
                r = val;
            }
            case 5 -> {
                b = 255 - val;
                r = 255;
            }
        }
        int a = 255;
        return (a << 24) + (r << 16) + (g << 8) + b;
    }
}
