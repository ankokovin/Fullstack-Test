import OrganizationNodeTemplate from './organization-node.template.html';

import OrganizationNodeController from './organization-node.controller';

let OrganizationNodeComponent = {
    componentName: 'organizationNodeComponent',
    template: OrganizationNodeTemplate,
    controller: OrganizationNodeController,
    bindings: {
        node: '<',
        visible: '=',
    }
}

export default OrganizationNodeComponent;