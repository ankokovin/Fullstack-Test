import OrganizationListComponent from './components/organization-list/organization-list.component';
import OrganizationListTreeComponent from './components/organization-list-tree/organization-list-tree.component';
import OrganizationCreateComponent from './components/organization-create/organization-create.component';
let OrganizationModule = angular.module('OrganizationModule',[]);

OrganizationModule.component(OrganizationListComponent.componentName, OrganizationListComponent);
OrganizationModule.component(OrganizationListTreeComponent.componentName, OrganizationListTreeComponent);
OrganizationModule.component(OrganizationCreateComponent.componentName, OrganizationCreateComponent);

export default OrganizationModule;