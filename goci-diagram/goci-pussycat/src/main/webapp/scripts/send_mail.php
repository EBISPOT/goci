<?php
if($_POST){
    $name = $_POST['name'];
    $email = $_POST['email'];
    $message = $_POST['text'];

//send email
    mail("goci-curators@ebi.ac.uk", "GWAS diagram feedback" .$email, $message);
}
?>