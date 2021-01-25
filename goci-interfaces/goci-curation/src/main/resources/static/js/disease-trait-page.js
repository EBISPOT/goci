class DiseaseTrait {


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


}

