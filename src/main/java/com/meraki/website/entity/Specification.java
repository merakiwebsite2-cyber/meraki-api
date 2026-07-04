package com.meraki.website.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specification
{
    private String width;
    private String composition;
    private String weight;
    private Repeat repeat;
    private String martindale;
    private CareInstructions careInstructions;
    private String pilling;
    private Boolean waterRepellent;
    private String flameRetardancy;
    private String attention = "Colors on the screen may be different from colors in reality!.";
    
}
