export default class WorkerItemCtrl{
    constructor($routeParams, WorkerService, $location, $scope) {
        "ngInject";
        this.new =  'id' in $routeParams;
        this.$scope = $scope; 
        this.$location = $location;
        this.WorkerService = WorkerService;
        this.id =  $routeParams.id;
        this.name_invalid = false;
        this.name_error_message = "Error";
        this.$scope.head_error_message = false;
        this.head_error_message = 'Невозможно указать элемент как родительский';
        this.head_error_id = this.id;
    }

    $onInit() {
        if (this.new) {
            this.WorkerService.get(this.id)
            .then((data)=>{
                this.name = data.workerName;
                this.head_id = data.headId;
                this.org_id = data.orgId;
                this.$scope.$apply();
            }, (error) => {
                if (error.status === 404) {
                    this.$location.path('/not-found');
                }
            });    
        }
    }

    create() {
        console.log(this.name, this.org_id, this.head_id);
        this.WorkerService.create(this.name, this.org_id, this.head_id).then(
            (response) => console.log('Nice') //TODO: show some message, redirect?
        );
    }

    update() {
        console.log(this.id, this.name, this.org_id, this.head_id);
        this.WorkerService.update(this.id, this.name,  this.org_id, this.head_id).then(
            (response) => console.log('Nice'), //TODO: show some message, redirect?
            (error) => {
                console.log(error);
                if (error.status === 404) {
                    console.log('Record was deleted');
                } else if (error.status === 400) {
                    if (error.data.message === 'Запись с таким именем уже сущесвует') {
                        this.name_invalid = true;
                        this.name_error_message = error.data.message;
                        this.$scope.$apply();
                    }else if (error.data.message === this.head_error_message) {
                        this.updateHeadError(error.data.id);    
                    }
                }
            }
        )
    }

    delete() {
        this.WorkerService.delete(this.id).then(
            (response) => console.log('Nice'), //TODO: show some message, redirect?
            (error) => {
                if (error.status === 404) {
                    alert('Record was already deleted');
                }else if (error.status === 403) {
                    alert('Record has children');                    
                }
            }
        )
    }

    updateHeadError(id) {
        this.head_error_id = id;   
        this.$scope.$apply();  
    }


    nameInputChange() {
        if (!this.name || this.name.length === 0) {
            if (!this.name_invalid) {
                this.name_invalid = true;
                this.name_error_message = 'Это обязательное поле'
            }
        } else {
            if (this.name_invalid) {
                this.name_invalid = false;
            }
        }
    }
}