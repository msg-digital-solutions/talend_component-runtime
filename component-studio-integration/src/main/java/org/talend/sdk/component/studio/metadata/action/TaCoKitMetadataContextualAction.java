/**
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.talend.sdk.component.studio.metadata.action;

import org.eclipse.swt.graphics.Image;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.repository.ui.actions.metadata.AbstractCreateAction;
import org.talend.sdk.component.server.front.model.ConfigTypeNode;
import org.talend.sdk.component.studio.i18n.Messages;
import org.talend.sdk.component.studio.metadata.node.ITaCoKitRepositoryNode;

/**
 * Base class for TaCoKit Metadata contextual actions.
 * Contextual action is action which may be launched from context menu (it appears, when user clicks right mouse
 * button).
 * Metadata is part of Studio Repository. Metadata stores Component configuration, in particular for Datastores and
 * Datasets.
 * Create Datastore/Dataset and edit Datastore/Dataset actions should be available for Tacokit component families.
 * Component family potentially may have several different types of Datasets. E.g. Azure family has blob, queue and
 * table Datasets.
 */
public abstract class TaCoKitMetadataContextualAction extends AbstractCreateAction {

    private static final int DEFAULT_WIZARD_WIDTH = 700;

    private static final int DEFAULT_WIZARD_HEIGHT = 400;

    protected ITaCoKitRepositoryNode repositoryNode;

    protected ConfigTypeNode configTypeNode;

    protected int getWizardWidth() {
        return DEFAULT_WIZARD_WIDTH;
    }

    protected int getWizardHeight() {
        return DEFAULT_WIZARD_HEIGHT;
    }

    protected String getCreateLabel() {
        return Messages.getString("TaCoKitConfiguration.action.createLabel", configTypeNode.getConfigurationType(), //$NON-NLS-1$
                configTypeNode.getDisplayName());
    }

    protected String getEditLabel() {
        return Messages.getString("TaCoKitConfiguration.action.editLabel", configTypeNode.getConfigurationType(), //$NON-NLS-1$
                configTypeNode.getDisplayName());
    }

    protected String getOpenLabel() {
        return Messages.getString("TaCoKitConfiguration.action.openLabel", configTypeNode.getConfigurationType(), //$NON-NLS-1$
                configTypeNode.getDisplayName());
    }

    protected String getNodeLabel() {
        return repositoryNode.getDisplayText();
    }

    @Override
    public Class getClassForDoubleClick() {
        return ConnectionItem.class;
    }

    /**
     * TODO implement it
     * Returns image shown near contextual menu item name. It should be family icon
     * 
     * @return metadata contextual action image
     */
    protected Image getNodeImage() {
        return null;
    }

}
