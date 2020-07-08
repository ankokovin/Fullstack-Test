export default class WorkerService {
    constructor($http) {
        "ngInject";
        this.$http = $http;
    }

    get(id) {
        return new Promise((resolve, reject) => {
            this.$http.get('api/worker/'+id)
            .then((response) => {
                console.log(response);
                var errors = [];
                if (response.data.id != id) {
                    errors.push('api returned record with wrong id');
                }
                if (errors.length) reject(errors);
                resolve(response.data);
            },(error) => {
                console.log(error);
                reject(error);
            })
        });
    }

    get_node(id) {
        return new Promise((resolve, reject) => {
            this.$http.get('api/worker/tree',
            {
                params:{
                    id: id
                }
            })
            .then((response) => {
                console.log(response);
                resolve(response.data);
            },(error) => {
                reject(error);
            });
        });
    }

    get_paged(page, pageSize, searchName, searchOrgName) {
        return new Promise((resolve, reject) =>{
            this.$http.get('api/worker',{ 
                params: {
                    page:page,
                    pageSize:pageSize,
                    searchName:searchName,
                    searchOrgName:searchOrgName
                }
            }).then(
                (response) => {
                    var errors = [];
                    if (response.data.page != page) errors.push('api returned wrong page');
                    if (response.data.pageSize != pageSize) errors.push('api returned wrong pageSize');
                    if (errors.length) reject(errors);
                    resolve(response.data);
                },
                (error) => reject(error)
            )
        });
    }

    create(name, org_id, head_id) {
        return new Promise((resolve, reject) => {
            this.$http.post('api/worker',{name:name, org_id:org_id, head_id:head_id}).then(
                (response) => {
                    console.log(response);
                    resolve(response);
                },
                (error) => {
                    console.log(error);
                    reject(error);
                }
            );
        })
    }

    update(id, name, org_id, head_id) {
        return new Promise((resolve, reject) => {
            this.$http.post('api/worker/update',{id:id, name:name, org_id:org_id, head_id:head_id}).then(
                (response) => {
                    console.log(response);
                    resolve(response);
                },
                (error) => {
                    console.log(error);
                    reject(error);
                }
            );
        });
    }

    delete(id) {
        return new Promise((resolve, reject) => {
            this.$http.delete('api/worker',{data:id.toString(),headers: {'Content-Type': 'application/json;charset=utf-8'}}).then(
                (response) => {
                    console.log(response);
                    resolve(response);
                },
                (error) => {
                    console.log(error);
                    reject(error);
                }
            )
        });
    }
}
