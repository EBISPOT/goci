/** Prototype from PDB repository. Orcid/THOR Task */

(function($){
   
    if (window.addEventListener) {
        addEventListener("message", clientEvent, false)
    } else {
        attachEvent("onmessage", clientEvent)
    }
    
    //claim list
    var getOrcidClaimList = function(){
        $.ajax({
            cache: false,
            url: "https://www.ebi.ac.uk/europepmc/hubthor/api/orcid/find/other-id-self:"+orcidClaimData.workExternalIdentifiers[0].workExternalIdentifierId,
            dataType: "json",
            success: function(orchidRespData) {
                var claimListText = "";
                var currentUserClaimLflag = false;
                
                if(orchidRespData['orcid-search-results']['num-found'] > 0){
                    
                    if(typeof thorApplicationNamespace != 'undefined'){
                        
                        if(orchidRespData['orcid-search-results']['num-found'] == 1 &&  thorApplicationNamespace.userData != null &&  thorApplicationNamespace.userData.orcId == orchidRespData['orcid-search-results']['orcid-search-result']['orcid-profile']['orcid-identifier']['path'] ){
                            claimListText = "";
                        }else{
                            for(var uli=0; uli < orchidRespData['orcid-search-results']['num-found']; uli++){
                                
                                if(orchidRespData['orcid-search-results']['num-found'] == 1){
                                    var userOrcId = orchidRespData['orcid-search-results']['orcid-search-result']['orcid-profile']['orcid-identifier']['path'];
                                    var userOrcUrl = orchidRespData['orcid-search-results']['orcid-search-result']['orcid-profile']['orcid-identifier']['uri'];
                                    var userFamilyName = orchidRespData['orcid-search-results']['orcid-search-result']['orcid-profile']['orcid-bio']['personal-details']['family-name']['value'];
                                    var userGivenNames = orchidRespData['orcid-search-results']['orcid-search-result']['orcid-profile']['orcid-bio']['personal-details']['given-names']['value'];
                                    var userOrcName = userGivenNames+' '+userFamilyName;
                                }else{
                                    var userOrcId = orchidRespData['orcid-search-results']['orcid-search-result'][uli]['orcid-profile']['orcid-identifier']['path'];
                                    var userOrcUrl = orchidRespData['orcid-search-results']['orcid-search-result'][uli]['orcid-profile']['orcid-identifier']['uri'];
                                    var userFamilyName = orchidRespData['orcid-search-results']['orcid-search-result'][uli]['orcid-profile']['orcid-bio']['personal-details']['family-name']['value'];
                                    var userGivenNames = orchidRespData['orcid-search-results']['orcid-search-result'][uli]['orcid-profile']['orcid-bio']['personal-details']['given-names']['value'];
                                    var userOrcName = userGivenNames+' '+userFamilyName;
                                }
                                
                                if(thorApplicationNamespace.userData != null &&  thorApplicationNamespace.userData.orcId == userOrcId){
                                    currentUserClaimLflag = true;
                                }else{
                                    claimListText += '<br><a href="'+userOrcUrl+'">'+userOrcName+'</a>';
                                }
                                
                            }
                            
                        }
                        
                        
                    }
                }
                
                if(claimListText == ""){
                    
                    if(thorApplicationNamespace.userData != null && thorApplicationNamespace.userData.works.length > 0){
                        var userWorks = thorApplicationNamespace.userData.works;
                        for(var wli=0; wli < userWorks.length; wli++){
                            if(userWorks[wli].workExternalIdentifiers[0].workExternalIdentifierType == 'other-id' &&
                                userWorks[wli].workExternalIdentifiers[0].workExternalIdentifierId == orcidClaimData.workExternalIdentifiers[0].workExternalIdentifierId){
                                currentUserClaimLflag = true;
                            }
                        }
                        
                        if(currentUserClaimLflag == true){
                            $('.thor_div_showIf_datasetNotClaimed').hide();
                            $('.thor_div_showIf_datasetAlreadyClaimed').show();
                        }else{
                            $('.thor_div_showIf_datasetNotClaimed').show();
                            $('.thor_div_showIf_datasetAlreadyClaimed').hide();
                        }
                        
                    }else{
                        $('.thor_div_showIf_datasetNotClaimed').show();
                        $('.thor_div_showIf_datasetAlreadyClaimed').hide();
                    }
                    
                    //$('.thor_div_showIf_datasetNotClaimed').hide();
                    //$('.thor_div_showIf_datasetAlreadyClaimed').show();
                }else{
                    
                    if(currentUserClaimLflag == true){
                        if(orchidRespData['orcid-search-results']['num-found'] == 2){
                            claimListText = "You and following ORCiD user have claimed "+orcidClaimData.workExternalIdentifiers[0].workExternalIdentifierId+" :"+claimListText;
                        }else{
                            claimListText = "You and following ORCiD users have claimed "+orcidClaimData.workExternalIdentifiers[0].workExternalIdentifierId+" :"+claimListText;
                        }
                        $('.thor_div_showIf_datasetAlreadyClaimed').hide();
                    }else{
                        claimListText = "Following ORCiD users have claimed "+orcidClaimData.workExternalIdentifiers[0].workExternalIdentifierId+" :"+claimListText;
                    }
                    
                    $('.thor_div_showIf_datasetAlreadyClaimedList').html(claimListText);
                    $('.thor_div_showIf_datasetAlreadyClaimedList').show();
                    
                }
                
            }
        });
    }
    
    //Function to handle notifications
    function clientEvent(event) {
        if ("thor.loading.complete" == event.data) {
            getOrcidClaimList();
            //if (thorApplicationNamespace.claimingInfoData.isUserLoggedIn == true){}
            //if (thorApplicationNamespace.claimingInfoData.isDataClaimed ==true){}
        }
    }
    
})(jQuery)