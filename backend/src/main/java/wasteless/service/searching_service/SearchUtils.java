package wasteless.service.searching_service;

import wasteless.model.Searchable;

import java.util.List;

public class SearchUtils {

    /**
     * Joins the contents of list 2 to list 1 by iterating through both lists.
     * Eliminates duplicates from list 2. And returns the results.
     * @param list1 List of Searchable objects
     * @param list2 Another List of Searchable objects
     * @return List 1, with all the non-duplicate searchable objects added to it
     */
    public static List<Searchable> joinTwoLists(List<Searchable> list1, List<Searchable> list2) {
        for (Searchable result2  : list2) {
            if (list1.stream().noneMatch(o -> o.getId() == result2.getId())) {
                list1.add(result2);
            }
        }
        return list1;
    }

    private SearchUtils() {
        throw new IllegalStateException("Utility Class");
    }
}
