import WorkerListTemplate from './worker-list.template.html';

let workerListComponent = {
    componentName: 'workerListComponent',
    template: WorkerListTemplate,
    controller: function LoadworkerList() {
        console.log('LoadworkerList')
    }
};

export default workerListComponent;