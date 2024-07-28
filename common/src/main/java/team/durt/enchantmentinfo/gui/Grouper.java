package team.durt.enchantmentinfo.gui;

import team.durt.enchantmentinfo.gui.group.HeadGroup;
import team.durt.enchantmentinfo.gui.group.InfoGroup;

import java.util.*;

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
        Map<InfoLink, List<Integer>> map = new HashMap<>();

        for (int i = 0; i < groups.size(); i++) {
            HeadGroup.PairGroup pair = groups.get(i);
            InfoGroup.All all = pair.getTail();
            for (InfoGroup<?> group : all.getChildList()) {
                for (InfoLink link : getContentLinksList(group)) {
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
     * Return List of {@link InfoLink InfoLinks} to each information Object in given {@link InfoGroup}.
     * {@link InfoLink} contains name of each class in row from given {@link InfoGroup} to end Object
     * going through every {@link InfoGroup} in given group's child list in its {@link InfoLink#link} field
     * and contains information Object itself in its {@link InfoLink#info} field.
     * {@link InfoLink#link} always starts with given info class name.
     * This method intended to be used in {@link #sort(List)}.
     * Specifying Link to end Object needed to store information about this Object's parent as {@link InfoGroup InfoGroups}
     * to be able to compare two different Object's of same class considering their parents.
     *
     * @see InfoLink
     * @see #sort(List)
     * @see InfoGroup
     * @see InfoGroup#content
     * @see InfoGroup.InfoGroupsContainer
     */
    private static List<InfoLink> getContentLinksList(InfoGroup<?> info) {
        List<InfoLink> list = new ArrayList<>();
        String infoLink = info.getClass().toString();
        for (Object o : info.getChildList()) {
            if (o instanceof InfoGroup<?> group) {
                for (InfoLink subLink : getContentLinksList(group)) {
                    list.add(subLink.appendLink(infoLink));
                }
            } else {
                list.add(new InfoLink(infoLink, o));
            }
        }
        return list;
    }

     /**
     * Stores Object and {@link String} link to it. Link is intended to contain {@link InfoGroup} class names.
     * Intended to be used in {@link #getContentLinksList(InfoGroup)} and {@link #sort(List)}.
     *
     * @see #getContentLinksList(InfoGroup)
     * @see #sort(List)
     */
    static class InfoLink {
        String link;
        Object info;

        InfoLink(String link, Object info) {
            this.link = link;
            this.info = info;
        }

        InfoLink appendLink(String link) {
            this.link = link + this.link;
            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InfoLink otherLink) {
                return otherLink.link.equals(this.link) && otherLink.info.equals(this.info);
            }
            return super.equals(obj);
        }

         @Override
         public int hashCode() {
             return link.hashCode();
         }
     }

    private static List<HeadGroup.PairGroup> groupList(List<HeadGroup.PairGroup> groups) {
        if (groups.isEmpty()) return new ArrayList<>();

        int i = 0;

        Stack<HeadGroup.PairGroup> stack = new Stack<>();
        stack.push(groups.get(i++));

        for (; i < groups.size(); i++) {
            HeadGroup.PairGroup group1 = groups.get(i);
            HeadGroup.PairGroup group2 = stack.pop();
            for (HeadGroup.PairGroup grouped : groupBasic(group2, group1)) {
                stack.push(grouped);
            }
        }

        return stack.stream().toList();
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