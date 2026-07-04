package com.meraki.website.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CareInstructions {
    private Boolean wash;
    private Boolean bleach;
    private Boolean dry;
    private Boolean iron;
    private Boolean dryClean;
}
