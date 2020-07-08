import WorkerService from './worker.service';

import workerListComponent from './components/worker-list/worker-list.component';
import workerListTreeComponent from './components/worker-list-tree/worker-list-tree.component';
import workerItemComponent from './components/worker-item/worker-item.component';
import WorkerPickerComponent from './components/worker-picker/worker-picker.component';
import WorkerNodeComponent from './components/worker-node/worker-node.component';

import WorkerListCtrl from './components/worker-list/worker-list.controller';
import WorkerListTreeCtrl from './components/worker-list-tree/worker-list-tree.controller';
import WorkerItemCtrl from './components/worker-item/worker-item.controller';
import WorkerPickerCtrl from './components/worker-picker/worker-picker.controller';
import WorkerNodeCtrl from './components/worker-node/worker-node.controller';


let workerModule = angular.module('workerModule',[]);

workerModule.service('WorkerService', WorkerService);


workerModule.component(workerListComponent.componentName, workerListComponent)
    .controller('WorkerListCtrl', WorkerListCtrl);
workerModule.component(workerListTreeComponent.componentName, workerListTreeComponent)
    .controller('WorkerListTreeCtrl', WorkerListTreeCtrl);
workerModule.component(workerItemComponent.componentName, workerItemComponent)
    .controller('WorkerItemCtrl', WorkerItemCtrl);
workerModule.component(WorkerPickerComponent.componentName, WorkerPickerComponent)
    .controller('WorkerPickerCtrl', WorkerPickerCtrl);
workerModule.component(WorkerNodeComponent.componentName, WorkerNodeComponent)
    .controller('WorkerNodeCtrl', WorkerNodeCtrl);


export default workerModule;