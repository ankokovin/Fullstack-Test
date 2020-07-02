import angular from 'angular';

import 'angular-route';

import organizationListModule from './organization-list/organization-list.module';
import config from './app.config';

if ( ON_TEST ) {
    require('angular-mocks/angular-mocks');
}

let app = angular.module('app', ["ngRoute" , organizationListModule.name]);
app.config(config);

export default app;