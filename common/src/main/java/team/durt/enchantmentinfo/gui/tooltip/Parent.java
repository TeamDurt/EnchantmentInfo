package team.durt.enchantmentinfo.gui.tooltip;

import java.util.List;

public interface Parent<T> {
    T addChild(T child);
    void setChildList(List<T> childList);
    List<T> getChildList();
}
