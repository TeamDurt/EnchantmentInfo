package team.durt.enchantmentinfo.gui.tooltip;

import java.util.List;

public interface Parent<T> {
    Object addChild(T child);
    Object setChildList(List<T> childList);
    List<T> getChildList();
}
