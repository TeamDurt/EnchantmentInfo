package team.durt.enchantmentinfo.gui.tooltip;

import java.util.List;

public interface Parent<T> {
    T addChild(T child);
    T setChildList(List<T> childList);
    List<T> getChildList();
}
