package com.snakyhy.snakymail.search.service;

import com.snakyhy.snakymail.search.vo.SearchParam;
import com.snakyhy.snakymail.search.vo.SearchResult;

public interface MallSearchService {
    /**
     *
     * @param param 返回检索的结果
     * @return  返回所有检索的结果
     */
    SearchResult search(SearchParam param);
}
