package com.zuma.sms.converter;

import com.zuma.sms.dto.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.ArrayList;

/**
 * author:ZhengXing
 * datetime:2017/10/17 0017 13:54
 * JPA的page对象转换为自定义的PageDTO对象
 */
@Slf4j
public class JPAPage2PageVOConverter {
    /**
     * 一对一转换
     * @param page
     * @param <T>
     * @return
     */
    public static <T> PageVO<T> convert(Page<T> page) {
        int pageNo = page.getNumber();

        PageVO<T> pageVO = new PageVO<>(++pageNo, page.getSize(), page.getTotalElements(), page.getTotalPages(), page.getContent());
        //防止list为空
        if(pageVO.getList() == null)
            pageVO.setList(new ArrayList<T>());
        //防止totalPage为0
        pageVO.setTotalPage(pageVO.getTotalPage() == 0 ? 1 : pageVO.getTotalPage());

        return pageVO;
    }
}
