class NavDrawer{

    static init(){

        $("#clickable-table tr").click(function(){
            $(this).addClass('selected').siblings().removeClass('selected');
        });

        const siteBody      = document.querySelector('body');
        const menuOpenBg    = document.querySelector('.ui-menu-open');
        const menuClose     = document.querySelector('.ui-menu-close');
        const menuDrawer    = document.querySelector('.navigation-drawer');
        var isActive        = false;

        document.onkeyup = function(e) {
            e = e || window.event;
            var charCode = (typeof e.which == "number")?e.which:e.keyCode;
            //console.log(e.keyCode);
            if (charCode === 27 && isActive) {
                menuClose.click();
            }
        };

        function addClasses(first, second) {
            first.classList.add('is-active');
            second.classList.add('is-active');
        }
        function removeClasses(first, second) {
            first.classList.remove('is-active');
            second.classList.remove('is-active');
        }

        document.querySelectorAll('.ui-menu-open').forEach((menuToggle) => {

            menuToggle.onclick = function toggleNav (event) {
                if (isActive === true) {
                    removeClasses(menuOpenBg,menuDrawer);
                    menuToggle.focus();
                    isActive = false;
                } else {
                    addClasses(menuOpenBg,menuDrawer);
                    menuClose.focus();
                    isActive = true;
                }
                // Pass data to detail based on id of the clicked row
                // This function must be implemented in the Page using this re-usable component
                loadDataInDetailedView(this.id);
            };

            menuClose.onclick = function closeNav (event) {
                if (isActive === true) {
                    removeClasses(menuOpenBg,menuDrawer);
                    isActive = false;
                } else {
                    addClasses(menuOpenBg,menuDrawer);
                    isActive = true;
                }
            }
        });
    }
}