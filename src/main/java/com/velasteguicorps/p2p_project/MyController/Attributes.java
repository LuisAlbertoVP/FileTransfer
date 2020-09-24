package com.velasteguicorps.p2p_project.MyController;

import com.velasteguicorps.p2p_project.Model.VO.Progress;

/**
 *
 * @author Luis Velastegui
 */
public interface Attributes {
    boolean isTransferEnabled();
    String getPath();
    Progress getProgress();
    boolean isChatEnabled();
}
