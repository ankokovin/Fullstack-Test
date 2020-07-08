export default class OrganizationService {
    constructor($http) {
        "ngInject";
        this.$http = $http;
    }

    get(id) {
        return new Promise((resolve, reject) => {
            this.$http.get('api/organization/'+id)
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
            this.$http.get('api/organization/tree',
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

    get_paged(page, pageSize, searchName) {
        return new Promise((resolve, reject) =>{
            this.$http.get('api/organization',{ 
                params: {
                    page:page,
                    pageSize:pageSize,
                    searchName:searchName,
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

    create(name, head_id) {
        return new Promise((resolve, reject) => {
            this.$http.post('api/organization',{name:name, org_id:head_id}).then(
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

    update(id, name, head_id) {
        return new Promise((resolve, reject) => {
            this.$http.post('api/organization/update',{id:id, name:name, org_id:head_id}).then(
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
}
