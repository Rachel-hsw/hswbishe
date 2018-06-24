package cn.edu.jssvc.xzh.rebuildclass.myinterface;

import java.util.List;

/**
 * Created by rachel on 2018/4/24.
 */

public interface PermissionListener {
    void granted();
    void denied(List<String> deniedList);
}
