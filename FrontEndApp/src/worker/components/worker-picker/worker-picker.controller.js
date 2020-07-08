export default class WorkerPickerCtrl {
    constructor(WorkerService, $scope) {
        "ngInject";
        this.WorkerService = WorkerService;
        this.$scope = $scope;
        this.showList = false;
        this.error_ids = new Set();
        this.reqired = false;
    }

    loadCur() {
        if (this.headid) {
            this.WorkerService.get(this.headid).then((data) => {
                this.searchName = data.workerName;
                console.log(this.searchName);
                document.getElementById('wor-search-name-field').value = this.searchName;
            })
        }
    }

    $onChanges(changesObj) {
        if (changesObj.message){
            this.message = changesObj.message.currentValue;
        }
        if (changesObj.errorid){
            if(changesObj.errorid.currentValue) this.error_ids.add(changesObj.errorid.currentValue.toString());
        }
        if (changesObj.errormsg){
            this.error_msg = changesObj.errormsg.currentValue;
        }
        if (changesObj.init){
            this.headid = changesObj.init.currentValue;
            console.log(this.headid);
            this.loadCur();
        }
        if (changesObj.required){
            this.reqired = changesObj.required.currentValue;
        }
        if (changesObj.org_id){
            this.org_id = changesObj.org_id.currentValue;
        }
    }

    select(id) {
        this.headid = id;
        this.searchName = this.$scope.workerList[id];
        this.showList = false;
    }

    load() {
        this.WorkerService.get_paged(1, 5, this.searchName, this.org_id).then((data) => {
            console.log(data);
            this.$scope.workerList = Object.fromEntries(data.list.slice(0,5).map(x=>[x.id, x.name]));
            this.headid = Object.keys(this.$scope.workerList).find(key => this.$scope.workerList[key] === this.searchName);
            this.$scope.$apply();
        });  
    }

    searchNameFocus() {
        this.showList = true;
        this.load();
    }

    searchNameBlur(event) {
        this.showList = false;
    }


    listItemClass(id) {
        if (this.error_ids.has(id)) return 'disabled';
        else if (id === this.headid) return 'active';
        return ''
    }

    inputClass() {
        if (this.headid) {
            if (this.error_ids.has(this.headid.toString())) return 'is-invalid';
            return 'is-valid';
        }
        if (this.reqired === true) return 'is-invalid';
        return '';
    }


}