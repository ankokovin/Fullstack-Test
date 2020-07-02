import workerListComponent from './components/worker-list.component';
import workerListTreeComponent from './components/worker-list-tree.component';
import workerCreateComponent from './components/worker-create.component';
let workerModule = angular.module('workerModule',[]);

workerModule.component(workerListComponent.componentName, workerListComponent);
workerModule.component(workerListTreeComponent.componentName, workerListTreeComponent);
workerModule.component(workerCreateComponent.componentName, workerCreateComponent);

export default workerModule;