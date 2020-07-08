import OrganizationService from './organization.service';

import OrganizationNodeComponent from './components/organization-node/organization-node.component';
import OrganizationListComponent from './components/organization-list/organization-list.component';
import OrganizationListTreeComponent from './components/organization-list-tree/organization-list-tree.component';
import OrganizationItemComponent from './components/organization-item/organization-item.component';
import OrganizationPickerComponent from './components/organization-picker/organization-picker.component';

import OrganizationNodeCtrl from './components/organization-node/organization-node.controller';
import OrganizationListCtrl from './components/organization-list/organization-list.controller';
import OrganizationListTreeCtrl from './components/organization-list-tree/organization-list-tree.controller';
import OrganizationItemCtrl from './components/organization-item/organization-item.controller';
import OrganizationPickerCtrl from './components/organization-picker/organization-picker.controller';


let OrganizationModule = angular.module('OrganizationModule',[]);

OrganizationModule.service('OrganizationService', OrganizationService);

OrganizationModule.component(OrganizationPickerComponent.componentName, OrganizationPickerComponent)
    .controller('OrganizationPickerCtrl', OrganizationPickerCtrl);

OrganizationModule.component(OrganizationNodeComponent.componentName, OrganizationNodeComponent)
    .controller('OrganizationNodeCtrl', OrganizationNodeCtrl);

OrganizationModule.component(OrganizationListComponent.componentName, OrganizationListComponent)
    .controller('OrganizationListCtrl', OrganizationListCtrl);

OrganizationModule.component(OrganizationListTreeComponent.componentName, OrganizationListTreeComponent)
    .controller('OrganizationListTreeCtrl',OrganizationListTreeCtrl);

OrganizationModule.component(OrganizationItemComponent.componentName, OrganizationItemComponent)
    .controller('OrganizationItemCtrl', OrganizationItemCtrl);


    
export default OrganizationModule;