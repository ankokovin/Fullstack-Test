export default class WorkerListTreeCtrl{
    constructor(WorkerService) {
        "ngInject";
        this.WorkerService = WorkerService;
        this.WorkerService.get_node().then((data) => {
            this.children = data.children;
            console.log(this.children);
        });
    }
}