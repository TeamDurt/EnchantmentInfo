package team.durt.enchantmentinfo.gui;

import java.util.List;

public interface Parent<T> {
    Parent<T> addChild(T child);
    Parent<T> setChildList(List<T> childList);
    List<T> getChildList();
}
