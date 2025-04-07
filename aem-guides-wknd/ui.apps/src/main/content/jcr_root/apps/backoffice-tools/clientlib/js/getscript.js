function debounce(func, delay) {
    let timer;
    return function (...args) {
        clearTimeout(timer);
        timer = setTimeout(() => {
            func.apply(this, args);
        }, delay);
    };
}

const searchObj = {
    searchVal: "",
    init: function () {
        const self = this;
        const debouncedApiCall = debounce(function () {
            self.searchVal = $(".search-bar__input").val();
            self.makeApiCall();
        }, 400);

        $(".search-bar__input").on("input", debouncedApiCall);
    },
    clearInput: function(){
        this.searchVal = "";
        $(".search-bar__input").val("");
    },
    makeApiCall: function () {
        const category = selectedCategory.val;
        const value = this.searchVal;
        const self = this;
        if (!value) {
            this.showNoData();
            return;
        }

        const apiUrl = `/bin/warranty/get-warranty.html?type=${category}&value=${value}`;
$.ajax({
    url: apiUrl,
    method: "GET",
    success: function (data) {
        const result = data;
        if (!result || result.length === 0) {
            self.showNoData();
            return;
        }

        const rows = result.map(item => `
            <tr>
                <td>${item.modelId || "-"}</td>
                <td>${item.productId || "-"}</td>
                <td>${item.dealerId || "-"}</td>
                <td>${item.startDate || "-"}</td>
                <td>${item.endDate || "-"}</td>
            </tr>
        `).join("");

        $(".search-results tbody").html(rows);
    },
    error: function () {
        self.showNoData();
    }
});
    },
    showNoData: function () {
        $(".search-results tbody").html(`
            <tr class="no-data">
                <td colspan="5" style="text-align: center;">No result found</td>
            </tr>
        `);
    },
    formatDate: function (dateStr) {
        if (!dateStr) return "-";
        const date = new Date(dateStr);
        return date.toLocaleDateString("en-GB");
    }

};

const selectedCategory = {
    val: "modelId",
    init: function () {
        const self = this;
        $("body").on("change", "#category", function () {
            searchObj.clearInput();
            const selectedVal = $(this).val();
            self.val = selectedVal;
            if (selectedVal === "startDate" || selectedVal === "endDate") {
                $(".search-bar__input").attr('type', 'date');
                $(".search-bar__input").attr("placeholder", "Select a date");
            } else {
                $(".search-bar__input").attr('type', 'search');
                $(".search-bar__input").attr("placeholder", `Enter ${categories[selectedVal]}`);
            }
        });
    },
};


const categories = {
    "modelId": "Model Id",
    "productId": "Product Id",
    "dealerId": "Dealer Id",
}

$(document).ready(function (){
    selectedCategory.init();
    searchObj.init();
    $(".no-data").hide();
});