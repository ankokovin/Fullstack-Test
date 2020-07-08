export default class OrganizationItemCtrl{
    constructor($routeParams, OrganizationService, $location, $scope) {
        "ngInject";
        this.new =  'id' in $routeParams;
        this.$scope = $scope; 
        this.$location = $location;
        this.OrganizationService = OrganizationService;
        this.id =  $routeParams.id;
        this.name_invalid = false;
        this.name_error_message = "Error";
        this.$scope.head_error_message = false;
        this.head_error_message = 'Невозможно указать элемент как родительский';
        this.head_error_id = this.id;
    }

    $onInit() {
        if (this.new) {
            this.OrganizationService.get(this.id)
            .then((data)=>{
                this.name = data.orgName;
                this.head_id = data.headOrgId;
            }, (error) => {
                if (error.status === 404) {
                    this.$location.path('/not-found');
                }
            });    
        }
    }

    create() {
        this.OrganizationService.create(this.name, this.head_id).then(
            (response) => console.log('Nice') //TODO: show some message, redirect?
        );
    }

    update() {
        this.OrganizationService.update(this.id, this.name, this.head_id).then(
            (response) => console.log('Nice'), //TODO: show some message, redirect?
            (error) => {
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
        this.OrganizationService.delete(this.id).then(
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