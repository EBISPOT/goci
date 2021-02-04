class Publication {

    static endpoint = `${this.getProxyPath()}api/v1`;

    static formEvents() {
        const CLICK_EVENT = 'click';
        const CHANGE_EVENT = 'change';
        document.querySelector('#upload-data-button').addEventListener(CLICK_EVENT, () => {
            Publication.uploadButtonAction()
        });
        document.querySelector('#bulk-upload').addEventListener(CHANGE_EVENT, () => {
            UI.loadText('output', 'Click on the Upload button', 'red', 'bulk-upload');
        });
    }

    static floatingActionButtonEvents() {
        const CLICK_EVENT = 'click';
        document.querySelector('#activate-publication-summary-view').addEventListener(CLICK_EVENT, () => {
            Publication.switchFormView('publication-summary-view');
        });
        document.querySelector('#activate-upload-form-view').addEventListener(CLICK_EVENT, () => {
            Publication.switchFormView('upload-form-view');
        });
        document.querySelector('#activate-form-operation-view').addEventListener(CLICK_EVENT, () => {
            Publication.switchFormView('form-operation-view');
        });
    }

    static switchFormView(selectedViewId) {
        ["publication-summary-view", "upload-form-view", "form-operation-view"]
            .map(view => {
                UI.hideRow(`${view}`);
            });
        UI.unHideRow(`${selectedViewId}`);
        window.scroll({top: 0, left: 0, behavior: 'smooth'});
    }

    static uploadButtonAction() {
        const componentId = 'output';
        const VALIDATION_OBJECT = {'bulk-upload': 'Upload File'};
        const URI = '/gwas/curation/api/v1/studies'; //`${this.endpoint}/studies`;

        if (Validation.validateInputs(VALIDATION_OBJECT) === "invalid") {
            return;
        }
        UI.loadText(componentId, 'File upload in Progress ...', 'green', 'bulk-upload');
        const formData = new FormData();
        const fileField = document.querySelector('input[type="file"]');
        formData.append('multipartFile', fileField.files[0]);
        let httpRequest = HttpRequestEngine.fileUploadRequest(URI, formData, 'POST');
        HttpRequestEngine.fetchRequest(httpRequest).then((data) => {
            console.log(data);
            UI.updateFileInput(componentId, 'File upload done! Click to Upload another file', 'black');
        });
    }

    static getProxyPath() {
        let lastIndex = window.location.pathname.lastIndexOf("/");
        return window.location.pathname.slice(0, lastIndex + 1);
    }

    static init(){
        Publication.formEvents();
        Publication.floatingActionButtonEvents();
    }

}
Publication.init();

