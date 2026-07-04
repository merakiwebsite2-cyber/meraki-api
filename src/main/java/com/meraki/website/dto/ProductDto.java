package com.meraki.website.dto;

import com.meraki.website.entity.Specification;
import com.meraki.website.entity.Variant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductDto
{
    private String id;
//    private String name;
//    private String description;
    private Specification specification;
    private String category;//curtain,rugs
    private String subCategory;
    private String collection;//Carent,greenlon
    private Variant defaultVariant;
    private List<Variant> variantList;
    private List<String> certificates;
}
