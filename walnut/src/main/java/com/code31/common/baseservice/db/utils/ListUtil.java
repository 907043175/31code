package com.code31.common.baseservice.db.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public final class ListUtil {
    private ListUtil(){

    }

    public static <F, T> List<T> transform(List<F> fromList, Function<? super
            F, ? extends T> function) {
        return Lists.newArrayList(Lists.transform(fromList, function));
    }

    public static <F> void iterateViaFunc(final Iterable<F> iterable,
                                          final Function<? super F, Void> function) {
        checkNotNull(iterable);
        Iterator<F> iterator = iterable.iterator();
        checkNotNull(iterator);
        checkNotNull(function);
        while (iterator.hasNext()) {
            function.apply(iterator.next());
        }
    }

}
