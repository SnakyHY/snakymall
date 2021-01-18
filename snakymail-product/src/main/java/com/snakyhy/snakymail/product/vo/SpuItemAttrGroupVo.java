package com.snakyhy.snakymail.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class SpuItemAttrGroupVo {

    private String groupName;

    //attrId,attrName,attrValue
    private List<Attr> attrs;

}