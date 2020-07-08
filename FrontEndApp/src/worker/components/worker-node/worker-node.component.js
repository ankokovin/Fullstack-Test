import WorkerNodeTemplate from './worker-node.template.html';

import WorkerNodeController from './worker-node.controller';

let WorkerNodeComponent = {
    componentName: 'workerNodeComponent',
    template: WorkerNodeTemplate,
    controller: WorkerNodeController,
    bindings: {
        node: '<',
        visible: '=',
    }
}

export default WorkerNodeComponent;