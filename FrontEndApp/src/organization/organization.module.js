import OrganizationListComponent from './components/organization-list/organization-list.component';
import OrganizationListTreeComponent from './components/organization-list-tree/organization-list-tree.component';
import OrganizationCreateComponent from './components/organization-create/organization-create.component';
import OrganizationListCtrl from './components/organization-list/organization-list.controller';

let OrganizationModule = angular.module('OrganizationModule',[]);

OrganizationModule.component(OrganizationListComponent.componentName, OrganizationListComponent).controller('OrganizationListCtrl', OrganizationListCtrl);
OrganizationModule.component(OrganizationListTreeComponent.componentName, OrganizationListTreeComponent);
OrganizationModule.component(OrganizationCreateComponent.componentName, OrganizationCreateComponent);

export default OrganizationModule;