package com.g4vrk.schedula.multi;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FoliaUtility {

    public boolean foliaServer() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (final ClassNotFoundException ignored) {
            return false;
        }
    }

}
