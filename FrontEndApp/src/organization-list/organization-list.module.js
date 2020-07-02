import organizationListComponent from './organization-list.component';

let organizationListModule = angular.module('organizationListModule', [])

organizationListModule.component("organizationListComponent", organizationListComponent);

export default organizationListModule;