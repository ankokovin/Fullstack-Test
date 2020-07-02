import OrganizationListComponent from './components/organization-list.component';
import OrganizationListTreeComponent from './components/organization-list-tree.component';
import OrganizationCreateComponent from './components/organization-create.component';
let OrganizationModule = angular.module('OrganizationModule',[]);

OrganizationModule.component(OrganizationListComponent.componentName, OrganizationListComponent);
OrganizationModule.component(OrganizationListTreeComponent.componentName, OrganizationListTreeComponent);
OrganizationModule.component(OrganizationCreateComponent.componentName, OrganizationCreateComponent);

export default OrganizationModule;