function validateId(type){
  const regex = /^[A-Z0-9]{10}$/;
  if(!regex.test($(`#${type}`).val())){
    $(`#${type}Error`).text("ID must be exactly 10 characters, using only capital letters and numbers.").show();
    return false;
  }
  $(`#${type}Error`).hide();
  warrantyForm.setWarranty(type,$(`#${type}`).val().trim());
  return true;
}
 
function validateDealerId(){
  const regex=/^\d{4}$/;
  if(!regex.test($("#dealerId").val())){
    $("#dealerError").text("Should contain only 4 digits!").show();
    return false;
  }
  $("#dealerError").hide();
  warrantyForm.setWarranty("dealerId",$("#dealerId").val().trim());
  return true;
}
 
function validateStartDate(){
  const startDate=$("#startDate").val();
  if(!startDate){
    $("#startDateError").text("Start date is required.").show();
    return false;
  }
  $("#startDateError").hide();
  warrantyForm.setWarranty("startDate",startDate);
  return true;
}
 
function validateEndDate(){
  const startDate=$("#startDate").val();
  const endDate=$("#endDate").val();
  if(!endDate){
    $("#endDateError").text("End date is required.").show();
    return false;
  }
  if(startDate&&new Date(endDate)<new Date(startDate)){
    $("#endDateError").text("End date cannot be before start date.").show();
    return false;
  }
  $("#endDateError").hide();
  warrantyForm.setWarranty("endDate",endDate);
  return true;
}
 
const warrantyForm={
  warranty:{},
  setWarranty:function(key,value){
    this.warranty[key]=value;
  },
  init:function(){
    $(document).on("input","#warrantyForm input",function(event){
      switch(event.target.id){
        case "modelId":validateId("modelId");break;
        case "productId":validateId("productId");break;
        case "dealerId":validateDealerId();break;
        case "startDate":validateStartDate();break;
        case "endDate":validateEndDate();break;
      }
    });
 
    $(document).on("submit","#warrantyForm",function(event){
      event.preventDefault();
      const isModelValid=validateId("modelId");
      const isProductValid=validateId("productId");
      const isDealerValid=validateDealerId();
      const isStartValid=validateStartDate();
      const isEndValid=validateEndDate();
      if(isModelValid&&isProductValid&&isDealerValid&&isStartValid&&isEndValid){
        $.ajax({
          url:"/bin/warranty/set-warranty.html",
          type:"POST",
          contentType:"application/json",
          dataType:"text",
          data:JSON.stringify(warrantyForm.warranty),
          success:function(){
            alert("Warranty added successfully! 🎉");
            $("#warrantyForm")[0].reset();
            warrantyForm.warranty={};
          },
          error:function(xhr){
            if(xhr.status===409){
              alert("Product Id already exists");
            }else{
              alert("Error while submitting data ❌");
            }
          }
        });
      }
    });
  }
};
 
$(document).ready(warrantyForm.init);
 