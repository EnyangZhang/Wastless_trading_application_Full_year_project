<template>
  <div id="sales-report" class="text-center">
    <b-row>
      <b-col
          style="text-align: left;"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select a reporting period</label>
        <b-form-select
            style="width: 100%"
            v-model="reportPeriod"
            :options="reportPeriodOptions">
        </b-form-select>
      </b-col>
      <b-col
          v-if="reportPeriod === 'Year'"
          style="text-align: left;"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select a year</label>
        <b-form-select
            style="width: 100%"
            v-model="selectedYear"
            :options="years">
        </b-form-select>
      </b-col>
      <b-col
          v-if="reportPeriod === 'Month'"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select a month</label>
        <Calendar v-model="selectedMonth"
                  view="month"
                  dateFormat="mm/yy"
                  :yearNavigator="true"
                  :year-range="yearRange"
                  :max-date="maxDate"/>
      </b-col>
      <b-col
          v-if="reportPeriod === 'Week'"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select start of week</label>
        <Calendar v-model="selectedWeekStart"
                  dateFormat="dd/mm/y"
                  :disabled-days="[1,2,3,4,5,6]"
                  :yearNavigator="true"
                  :year-range="yearRange"
                  :max-date="maxDate"/>
      </b-col>
      <b-col
          v-if="reportPeriod === 'Day'"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select a day</label>
        <Calendar v-model="selectedDay"
                  dateFormat="dd/mm/y"
                  :yearNavigator="true"
                  :year-range="yearRange"
                  :max-date="maxDate"/>
      </b-col>
      <b-col
          v-if="reportPeriod === 'Custom Period'"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select start date</label>
        <Calendar v-model="selectedCustomRangeStart"
                  dateFormat="dd/mm/y"
                  :yearNavigator="true"
                  :year-range="yearRange"
                  :max-date="selectedCustomRangeEnd"/>
      </b-col>
      <b-col
          v-if="reportPeriod === 'Custom Period'"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select end date</label>
        <Calendar v-model="selectedCustomRangeEnd"
                  dateFormat="dd/mm/y"
                  :yearNavigator="true"
                  :year-range="yearRange"
                  :min-date="selectedCustomRangeStart"/>
      </b-col>
      <b-col
          v-if="reportPeriod !== null"
          style="text-align: left;"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <label style="width: 100%; text-align: left">Select report granularity</label>
        <b-form-select
            style="width: 100%"
            v-model="granularity"
            :options="granularityOptions">
        </b-form-select>
      </b-col>
      <b-col
          align-self-end
          v-if="reportPeriod !== null"
          cols="12"
          lg="2"
          md="4"
          sm="6">
        <b-button
            style="margin-top: 32px"
            :disabled="disableGenerateButton"
            id="generate-button"
            @click="generateReport"
            variant="primary">Generate report</b-button>
      </b-col>
    </b-row>
    <br>
    <b-card
        style="text-align: left;"
        v-if="report !== null">
      <p><strong>{{`Sales Report for ${report.business.name}`}}</strong></p>
      <p><em>{{`Report period from ${formatDate(report.periodStart)} to ${formatDate(report.periodEnd)}`}}</em></p>
      <b-table :items="report.sections"></b-table>
    </b-card>
  </div>
</template>

<script>

import storage_util from "@/javascript_modules/storage_util";
import api from "@/Api";

export default {
  name: "SalesReport",

  data() {
    return {
      reportPeriodOptions: [
        {value: "Year", text: "Year"},
        {value: "Month", text: "Month"},
        {value: "Week", text: "Week"},
        {value: "Day", text: "Day"},
        {value: "Custom Period", text: "Custom Period"},
      ],

      reportPeriod: null,
      selectedYear: null,
      selectedMonth: null,
      selectedWeekStart: null,
      selectedDay: null,
      selectedCustomRangeStart: null,
      selectedCustomRangeEnd: null,
      years: null,
      yearRange: null,

      businessId: null,
      maxDate: null,

      granularity: "Total",
      granularityOptions: [
          "Total",
          "Monthly"
      ],

      report: null
    }
  },

  async mounted() {
    await this.getBusinessInfo();
  },

  computed: {

    /**
     * Disables the generate button unless the period (or start and end dates) have been selected.
     */
    disableGenerateButton() {
      return this.granularity === null ||
          this.reportPeriod === null ||
          this.reportPeriod === "Year" && this.selectedYear === null ||
          this.reportPeriod === "Month" && this.selectedMonth === null ||
          this.reportPeriod === "Week" && this.selectedWeekStart === null ||
          this.reportPeriod === "Day" && this.selectedDay === null ||
          this.reportPeriod === "Custom Period" &&
            (this.selectedCustomRangeStart === null
            || this.selectedCustomRangeEnd === null);

    }
  },

  methods: {

    /**
     * Figures out the period start and end based on the selected period.
     * @returns {*[]}
     */
    getPeriodStartAndEnd() {
      let periodStart = null;
      let periodEnd = null;
      if (this.reportPeriod === "Year") {
        periodStart = new Date(0);
        periodEnd = new Date(0);
        periodStart.setFullYear(this.selectedYear);
        periodStart.setDate(2);
        periodEnd.setFullYear(this.selectedYear + 1);
        periodEnd.setDate(1);
      } else if (this.reportPeriod === "Month") {
        let month = this.selectedMonth.getMonth();
        let year = this.selectedMonth.getFullYear();
        periodStart = new Date(year, month, 2);
        periodEnd = new Date(year, month + 1, 1);
      } else if (this.reportPeriod === "Week") {
        let date = this.selectedWeekStart.getDate();
        let month = this.selectedWeekStart.getMonth();
        let year = this.selectedWeekStart.getFullYear();
        periodStart = new Date(year, month, date + 1);
        periodEnd = new Date(year, month, date + 7);
      } else if (this.reportPeriod === "Day") {
        let date = this.selectedDay.getDate();
        let month = this.selectedDay.getMonth();
        let year = this.selectedDay.getFullYear();
        periodStart = new Date(year, month, date + 1);
        periodEnd = periodStart;
      } else if (this.reportPeriod === "Custom Period") {
        let date1 = this.selectedCustomRangeStart.getDate();
        let month1 = this.selectedCustomRangeStart.getMonth();
        let year1 = this.selectedCustomRangeStart.getFullYear();
        periodStart = new Date(year1, month1, date1 + 1);

        let date2 = this.selectedCustomRangeEnd.getDate();
        let month2 = this.selectedCustomRangeEnd.getMonth();
        let year2 = this.selectedCustomRangeEnd.getFullYear();
        periodEnd = new Date(year2, month2, date2 + 1);
      }
      if (periodStart === null || periodEnd === null) {
        this.$bvToast.toast(`${"Please reload the page and try again"}`, {
          title: 'Unexpected report period ',
          variant: 'danger',
          toaster: 'b-toaster-bottom-right',
          autoHideDelay: 5000,
          appendToast: true
        });
      }
      return [periodStart, periodEnd];
    },

    /**
     * Creates request to generate report data and sets this.report to equal the response data
     * @returns {Promise<void>}
     */
    async generateReport() {
      const periods = this.getPeriodStartAndEnd();
      const periodStartString = periods[0].toISOString().split('T')[0];
      const periodEndString = periods[1].toISOString().split('T')[0];
      const granularity = this.granularity.toUpperCase();
      try {
        const result = await api.getSalesReport(this.businessId, periodStartString, periodEndString, granularity);
        let newSections = [];
        for (let resultSection of result.data.sections) {
          if (resultSection.name === "TOTAL") {
            let section = {
              name: "Whole period",
              numberOfSales: resultSection.numberOfSales,
              valueOfSales: resultSection.valueOfSales
            }
            newSections.push(section)
          } else {
            let section = {
              name: resultSection.name,
              numberOfSales: resultSection.numberOfSales,
              valueOfSales: resultSection.valueOfSales,
              period: `${this.formatDate(resultSection.sectionStart)} to ${this.formatDate(resultSection.sectionEnd)}`
            }
            newSections.push(section)
          }
        }
        result.data.sections = newSections;
        this.report = result.data;
      } catch (error) {
        this.$bvToast.toast(`${error.response.data.message}`, {
          title: 'Error generating report',
          variant: 'danger',
          toaster: 'b-toaster-bottom-right',
          autoHideDelay: 5000,
          appendToast: true
        });
      }
    },

    /**
     * Function to call the method to get data that the parent is able to call for any related component.
     **/
    refreshData() {
      if (!this.disableGenerateButton && this.report !== null && this.report !== undefined) {
        this.generateReport();
      }
      // Implement whatever methods are needed to update the component's data.
    },

    /**
     * Sets yearRange from the year the business was created to the current year. Sets selected year to the year the
     * business was created.
     */
    setYears() {
      const today = new Date();
      let start = 1900;
      const stop = today.getFullYear();
      let years = [];
      while (start <= stop) {
        years.push(start);
        start++;
      }
      this.years = years;
      this.selectedYear = years[years.length - 1];
      this.yearRange = years[0] + ":" + years[years.length - 1];
    },

    /**
     * Gets the most recent Sunday to the given date
     * @param date
     * @returns {Date}
     */
    lastSunday(date) {
      date.setDate(date.getDate() - date.getDay());
      return date;
    },

    /**
     * Accepts a date string of format yyyy-mm-dd and returns a date string with format dd/mm/yy
     * @param dateString
     */
    formatDate(dateString) {
      let splitDate = dateString.split("-");
      return splitDate[2] + "/" + splitDate [1] + "/" +  splitDate[0].substring(2);
    },

    /**
     * Sets limit for max and min date and sets default selected day week month and custom range.
     */
    setDateLimits() {
      this.maxDate = new Date();
      this.selectedMonth = this.maxDate;
      this.selectedWeekStart = this.lastSunday(this.maxDate);
      this.selectedDay = this.maxDate;
      let defaultStart = new Date(this.maxDate);
      let defaultEnd = new Date(this.maxDate);
      defaultStart.setDate(defaultStart.getDate() - 1);
      this.selectedCustomRangeStart = defaultStart;
      this.selectedCustomRangeEnd = defaultEnd;
    },

    /**
     * Function that gets business by sending request to API given bueId, and sets data fields
     * according to response.
     **/
    getBusinessInfo: async function() {
      let id;
      if (this.$route.name === "ManageBusiness") {
        id = storage_util.getActingAs();
      } else {
        id = this.$route.params.id;
        this.showListings = true;
      }
      this.businessId = id;
      this.setYears();
      this.setDateLimits();
    },
  }
}
</script>

<style scoped>

</style>