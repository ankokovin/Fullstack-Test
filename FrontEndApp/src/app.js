import angular from 'angular';

import 'angular-route';

import OrganizationModule from './organization/organization.module';
import WorkerModule from './worker/worker.module';
import routingConfig from './app.routing';

if ( ON_TEST ) {
    require('angular-mocks/angular-mocks');
}

let app = angular.module('app', ["ngRoute" , OrganizationModule.name, WorkerModule.name]);
app.config(routingConfig);

export default app;