package io.john.amiscaray.quak.generator.jpms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ParsedPackageInfo {

    private String packageName;
    private boolean containsORMClasses;
    private boolean containsRestModelClasses;
    private boolean containsDIClasses;

}
