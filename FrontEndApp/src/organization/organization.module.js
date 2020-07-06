import OrganizationNodeComponent from './components/organization-node/organization-node.component';
import OrganizationListComponent from './components/organization-list/organization-list.component';
import OrganizationListTreeComponent from './components/organization-list-tree/organization-list-tree.component';
import OrganizationCreateComponent from './components/organization-create/organization-create.component';

import OrganizationNodeCtrl from './components/organization-node/organization-node.controller';
import OrganizationListCtrl from './components/organization-list/organization-list.controller';
import OrganizationListTreeCtrl from './components/organization-list-tree/organization-list-tree.controller';

let OrganizationModule = angular.module('OrganizationModule',[]);

OrganizationModule.component(OrganizationNodeComponent.componentName, OrganizationNodeComponent)
    .controller('OrganizationNodeCtrl', OrganizationNodeCtrl);

OrganizationModule.component(OrganizationListComponent.componentName, OrganizationListComponent)
    .controller('OrganizationListCtrl', OrganizationListCtrl);

OrganizationModule.component(OrganizationListTreeComponent.componentName, OrganizationListTreeComponent)
    .controller('OrganizationListTreeCtrl',OrganizationListTreeCtrl);

OrganizationModule.component(OrganizationCreateComponent.componentName, OrganizationCreateComponent);

export default OrganizationModule;