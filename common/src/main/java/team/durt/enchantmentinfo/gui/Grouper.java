package team.durt.enchantmentinfo.gui;

import team.durt.enchantmentinfo.gui.group.HeadGroup;
import team.durt.enchantmentinfo.gui.group.InfoGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grouper {
    public static List<HeadGroup.PairGroup> group(List<HeadGroup.PairGroup> groups) {
        List<HeadGroup.PairGroup> sorted = sort(groups); // first, sorting to get more convenient list
        return groupList(sorted); // second, grouping elements in sorted list
    }

    /**
     * Sorts given List in most convenient way for grouping
     * where combinable elements are located nearby
     * and combinations sorted in descending order.
     *
     * @see #group(List)
     */
    private static List<HeadGroup.PairGroup> sort(List<HeadGroup.PairGroup> groups) {
        Map<String, List<Integer>> map = new HashMap<>();

        for (int i = 0; i < groups.size(); i++) {
            HeadGroup.PairGroup pair = groups.get(i);
            InfoGroup.All all = pair.getTail();
            for (InfoGroup<?> group : all.getChildList()) {
                for (String link : getContentLinksList(group)) {
                    if (!map.containsKey(link)) {
                        map.put(link, new ArrayList<>(List.of(i)));
                    } else {
                        map.get(link).add(i);
                    }
                }
            }
        }

        List<List<Integer>> sortedIndexes = map.values()
                .stream()
                .sorted((list1, list2) -> {
                    int result = list2.size() - list1.size();
                    if (result != 0) return result;
                    for (int i = 0; i < list1.size(); i++) {
                        int newResult = list1.get(i) - list2.get(i);
                        if (newResult != 0) return newResult;
                    }
                    return result;
                }) // sorting lists by their sizes from most to least, and their content from least to most
                .toList();

        List<HeadGroup.PairGroup> sorted = new ArrayList<>();
        for (List<Integer> indexes : sortedIndexes) {
            for (Integer index : indexes) {
                if (!sorted.contains(groups.get(index))) {
                    sorted.add(groups.get(index));
                }
            }
        }

        return sorted;
    }

    /**
     * Return List of "Links" to each information Object in info.
     * "Link" is a {@link String} that contains name of each class in row from info to end Object
     * going through every {@link InfoGroup} in given group's child list
     * and ends with {@link String} gotten from {@link Object#toString()} method for this information Object.
     * Link always starts with given info class name.
     * This method intended to be used in {@link #sort(List)}.
     * Specifying Link to end Object needed to store information about this Object's parent as {@link InfoGroup InfoGroups}
     * to be able to compare two different Object's of same class considering their parents.
     *
     * @see #sort(List)
     * @see InfoGroup
     * @see InfoGroup#content
     * @see InfoGroup.InfoGroupsContainer
     */
    // todo add object itself to link for comparing via equals
    private static List<String> getContentLinksList(InfoGroup<?> info) {
        List<String> list = new ArrayList<>();
        String infoLink = info.getClass().toString();
        for (Object o : info.getChildList()) {
            if (o instanceof InfoGroup<?> group) {
                for (String subLink : getContentLinksList(group)) {
                    list.add(infoLink + subLink);
                }
            } else {
                list.add(infoLink + o.toString());
            }
        }
        return list;
    }

    private static List<HeadGroup.PairGroup> groupList(List<HeadGroup.PairGroup> groups) {
        List<HeadGroup.PairGroup> grouped = groupListBasic(groups); // one-step grouping
        List<HeadGroup.PairGroup> newGrouped = groupListBasic(grouped);
        // repeat until getting best grouped list
        while (newGrouped.size() != grouped.size()) { // stop when nothing changes
            grouped = newGrouped;
            newGrouped = groupListBasic(newGrouped);
        }
        return newGrouped;
    }

    private static List<HeadGroup.PairGroup> groupListBasic(List<HeadGroup.PairGroup> groups) {
        List<HeadGroup.PairGroup> grouped = new ArrayList<>();
        for (int i = 0; i < groups.size(); i+=2) {
            // if current element has no pair, just pull it further
            if (i + 1 == groups.size()) {
                grouped.add(groups.get(i));
                break;
            }
            // comparing by pairs
            grouped.addAll(groupBasic(groups.get(i), groups.get(i + 1)));
        }
        return grouped;
    }

    private static List<HeadGroup.PairGroup> groupBasic(HeadGroup.PairGroup group1, HeadGroup.PairGroup group2) {
        InfoGroup.All similarPart = getSimilar(group1, group2); // info that both groups have
        if (similarPart.getChildList().isEmpty()) return List.of(group1, group2);
        group1.extract(similarPart); // extracting duplicated info
        group2.extract(similarPart);
        HeadGroup.PairGroup pair = new HeadGroup.PairGroup(
                new HeadGroup.HeadPairListGroup(List.of(group1, group2)),
                similarPart
        ); // todo squash lists
        return List.of(pair);
    }

    private static InfoGroup.All getSimilar(HeadGroup.PairGroup group1, HeadGroup.PairGroup group2) {
        return group1.getSimilarInfo(group2.getTail());
    }
}
