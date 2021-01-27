class DiseaseTrait {

    static getTraitData(pageNumber) {
        const URI = `/api/v1/disease-traits?page=${--pageNumber}`;
        let httpRequest = HttpRequestEngine.requestWithoutBody(URI, 'GET');
        HttpRequestEngine.fetchRequest(httpRequest).then((data) => {
            // UI.hideRow('loader-row');
            UI.removeChildren('data-list');
            let columns = ['trait'];
            data._embedded.diseaseTraits.map(data => {
                DiseaseTrait.updateDataRows(data, data.id, columns, 'data-list');
            });
            DiseaseTrait.generatePagination(data.page);
        });
    }

    static updateDataRows(dataObject, dataId, properties, tableBodyId) {
        const tableRows = document.querySelector(`#${tableBodyId}`);
        const tr = document.createElement('tr');
        tr.setAttribute('id', `row-${dataId}`);
        //blink
        properties.forEach((property, index, properties) => {
            let td = UI.tableData(dataObject[property], `col-${index}-${dataId}`);
            td.setAttribute("class", "ui-menu-open");
            tr.appendChild(td);
            if (Object.is(properties.length - 1, index)) {
                td = document.createElement("td");
                let deleteButton = UI.buttonElement('Delete',`deleteBtn-${dataId}`, 'fa-trash-alt');
                td.appendChild(deleteButton);
                tr.appendChild(td);
            }
        });
        tableRows.prepend(tr);

        // Delete Event : Event Listener delete Button in the last column
        document.querySelector(`#deleteBtn-${dataId}`).addEventListener('click', function () {
            let uri = `/api/v1/disease-traits/${dataId}`;
            let httpRequest = HttpRequestEngine.requestWithoutBody(uri, 'DELETE');
            HttpRequestEngine.fetchRequest(httpRequest).then((result) => {
                let row = document.querySelector(`#row-${dataId}`);
                row.remove();
            });
        });

        NavDrawer.init();
    }

    static generatePagination(pageData){

        let size = pageData.size;
        let totalPages = pageData.totalPages;
        let totalElements = pageData.totalElements;
        let page = pageData.number + 1;
        let currentIndex = pageData.number + 1;
        let beginIndex = Math.max(1, page - 4);
        let endIndex = Math.min(beginIndex + 7, totalPages);

        let pageList = document.createElement('ul');
        pageList.setAttribute('class', 'pagination pagination-sm');

        // First Pagination Link
        let firstPage = document.createElement("li");
        let firstLink = document.createElement("a");
        let firstText = document.createTextNode('First «');
        if (currentIndex === 1){
            firstPage.setAttribute('class', 'disabled');
        }else {
            firstLink.style.cursor = "pointer";
            firstLink.addEventListener("click",  ()=>{
                DiseaseTrait.getTraitData(1);
            });
        }
        firstLink.appendChild(firstText);
        firstPage.appendChild(firstLink);
        pageList.appendChild(firstPage);

        // Next Pagination Links:
        for (let i=beginIndex; i<endIndex; i++ ){
            let nextPage = document.createElement("li");
            let nextLink = document.createElement("a");
            let nextPageText = document.createTextNode(i);
            if (i === page){
                nextPage.setAttribute('class', 'active');
            }else {
                nextLink.style.cursor = "pointer";
                nextLink.addEventListener("click",  ()=>{
                    DiseaseTrait.getTraitData(i);
                });
            }
            nextLink.appendChild(nextPageText);
            nextPage.appendChild(nextLink);
            pageList.appendChild(nextPage);
        }

        // Last Pagination Link:
        //let pageId = 'finalPage';
        let finalPage = document.createElement("li");
        let finalPageLink = document.createElement("a");
        finalPageLink.style.cursor = "pointer";
        //finalPageLink.setAttribute('id', pageId);
        let finalPageText = document.createTextNode('Last «');
        if (page === totalPages){
            finalPage.setAttribute('class', 'disabled');
        }else {
            finalPageLink.addEventListener("click",  ()=>{
                DiseaseTrait.getTraitData(totalPages);
            });
        }
        finalPageLink.appendChild(finalPageText);
        finalPage.appendChild(finalPageLink);
        pageList.appendChild(finalPage);

        UI.removeChildren('page-area');
        const dPagination = document.querySelector('#page-area');
        dPagination.appendChild(pageList);
    }

    static formEvents(){
        const CLICK_EVENT = 'click';
        const CHANGE_EVENT = 'change';
        document.querySelector('#create-form-button').addEventListener(CLICK_EVENT, DiseaseTrait.save);
        document.querySelector('#upload-data-button').addEventListener(CLICK_EVENT, DiseaseTrait.uploadButtonAction);
        document.querySelector('#analysis-data-button').addEventListener(CLICK_EVENT, DiseaseTrait.analysisButtonAction);
        document.querySelector('#bulk-upload').addEventListener(CHANGE_EVENT,  () => {
            UI.loadText('output', 'Click on the Upload button', 'red', 'bulk-upload');
        });
        document.querySelector('#analysis-uploads').addEventListener(CHANGE_EVENT, () => {
            UI.loadText('analysis', 'Click on the Upload button', 'red', 'analysis-uploads');
        });
        document.querySelector('#edit-form-button').addEventListener(CLICK_EVENT, DiseaseTrait.edit);
    }

    static floatingActionButtonEvents(){
        const CLICK_EVENT = 'click';
        document.querySelector('#activate-analysis-form-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('analysis-form-view');
        });
        document.querySelector('#activate-upload-form-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('upload-form-view');
        });
        document.querySelector('#activate-add-form-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('add-form-view');
        });
        document.querySelector('#activate-visualization-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('visualization-view');
        });
        document.querySelector('#activate-trait-table-view').addEventListener(CLICK_EVENT, () => {
            DiseaseTrait.switchFormView('trait-table-view');
        });
    }

    static switchFormView(selectedViewId) {
        let views = ["add-form-view", "upload-form-view", "analysis-form-view", "visualization-view", "trait-table-view"];
        views.map(view => {
            UI.hideRow(`${view}`);
        });
        UI.unHideRow(`${selectedViewId}`);
    }

    static uploadButtonAction() {
        //Validate Inputs
        const componentId = 'output';
        const VALIDATION_OBJECT = {'bulk-upload': 'Upload File'};
        const URI = '/api/v1/disease-traits/uploads';
        if (Validation.validateInputs(VALIDATION_OBJECT) === "invalid") {
            return;
        }
        UI.loadText(componentId,'File upload in Progress ...','green','bulk-upload');
        // Serialize form values let dataObject = document.querySelector('#uploads').value;

        const formData = new FormData();
        const fileField = document.querySelector('input[type="file"]');
        formData.append('multipartFile', fileField.files[0]);
        // Upload data file to Storage
        let httpRequest = HttpRequestEngine.fileUploadRequest(URI, formData, 'POST');
        HttpRequestEngine.fetchRequest(httpRequest).then((data) => {
            console.log(data);
            UI.updateFileInput(componentId,'File upload done! Click to Upload another file','black');
        });
    }

    static analysisButtonAction() {
        //Validate Inputs
        const componentId = 'analysis';
        const VALIDATION_OBJECT = {'analysis-uploads': 'Analysis File'};
        const URI = '/api/v1/disease-traits/analysis';
        if (Validation.validateInputs(VALIDATION_OBJECT) === "invalid") {
            return;
        }
        UI.loadText(componentId,'Analysis File upload in Progress ...','green', 'analysis-uploads');

        const formData = new FormData();
        const fileField = document.querySelector('#analysis-uploads');
        formData.append('multipartFile', fileField.files[0]);
        let httpRequest = HttpRequestEngine.fileUploadRequest(URI, formData, 'POST');
        HttpRequestEngine.fetchRequest(httpRequest).then((data) => {
            console.log(data);
            UI.updateFileInput(componentId,'File upload done! Click to Analyse another file','black');
        });
    }

    static save() {
        //Validate Inputs
        const VALIDATION_OBJECT = {'trait': 'Reported Trait'};
        const URI = '/api/v1/disease-traits';
        if (Validation.validateInputs(VALIDATION_OBJECT) === "invalid") {
            return;
        }
        let dataObject = $('#add-form-view :input').serializeJSON();
        let httpRequest = HttpRequestEngine.requestWithBody(URI, dataObject, 'POST');
        HttpRequestEngine.fetchRequest(httpRequest).then((data) => {
            let columns = ['trait'];
            DiseaseTrait.updateDataRows(data, data.id, columns, 'data-list');
            UI.clearFields(columns);
        });
    }

    static edit() {
        const VALIDATION_OBJECT = {'edit-trait': 'Updated Trait'};
        if (Validation.validateInputs(VALIDATION_OBJECT) === "invalid") {
            return;
        }
        let newTraitValue = document.querySelector('#edit-trait').value;
        let tableDataId = document.querySelector('#id-of-master').value;
        document.querySelector(`#${tableDataId}`).innerHTML = newTraitValue;
        let dataId = tableDataId.split("-")[2];

        let dataObject = $('#edit-form-view :input').serializeJSON();
        const uri = `/api/v1/disease-traits/${dataId}`;
        let httpRequest = HttpRequestEngine.requestWithBody(uri, dataObject, 'PUT');
        console.log(dataObject)
        HttpRequestEngine.fetchRequest(httpRequest).then((data) => {
            console.log(data);
            UI.launchToastNotification('Disease Trait was updated');
        });
    }
}

DiseaseTrait.getTraitData(1);
DiseaseTrait.formEvents();
DiseaseTrait.floatingActionButtonEvents();

function loadDataInDetailedView(componentId){

    let masterRowDOM = document.querySelector(`#${componentId}`);
    let detailViewTextDisplay = document.querySelector('#selected-trait');
    let detailViewTextFieldComponent = document.querySelector('#edit-trait');
    let detailViewHiddenFieldComponent = document.querySelector('#id-of-master');

    let trait = masterRowDOM.innerHTML;
    detailViewTextFieldComponent.value = trait;
    detailViewHiddenFieldComponent.value = componentId; //send td id to Nav Drawer so it can change the content
    detailViewTextDisplay.innerHTML = trait[0].toUpperCase()+trait.slice(1);
}

