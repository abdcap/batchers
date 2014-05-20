package be.cegeka.batchers.taxcalculator.application.domain;

import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaDateTimeSerializer;
import be.cegeka.batchers.taxcalculator.application.util.jackson.JodaMoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.joda.money.Money;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@NamedQueries({
        @NamedQuery(name = TaxCalculation.FIND_BY_MONTH_AND_YEAR, query = TaxCalculation.FIND_BY_MONTH_AND_YEAR_QUERY),
        @NamedQuery(name = TaxCalculation.FIND_BY_EMPLOYEE, query = TaxCalculation.FIND_BY_EMPLOYEE_QUERY)
})

@Entity
public class TaxCalculation {

    public static final String FIND_BY_MONTH_AND_YEAR = "TaxCalculation.FIND_BY_MONTH_AND_YEAR";
    public static final String FIND_BY_MONTH_AND_YEAR_QUERY = "SELECT tc FROM TaxCalculation tc " +
            " WHERE tc.month = :month AND tc.year = :year";

    public static final String FIND_BY_EMPLOYEE = "TaxCalculation.FIND_BY_EMPLOYEE";
    public static final String FIND_BY_EMPLOYEE_QUERY = "SELECT tc FROM TaxCalculation tc " +
            " WHERE tc.employee.id = :employeeId";

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NotNull
    private Employee employee;

    @Min(1)
    @Max(12)
    @NotNull
    private int month;

    @NotNull
    private int year;

    @JsonSerialize(using = JodaMoneySerializer.class)
    @Type(type = "org.jadira.usertype.moneyandcurrency.joda.PersistentMoneyAmount",
            parameters = {@Parameter(name = "currencyCode", value = "EUR")})
    @NotNull
    private Money tax;

    @JsonSerialize(using = JodaDateTimeSerializer.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @NotNull
    private DateTime calculationDate;

    public static TaxCalculation from(Employee employee, int year, int month, Money tax, DateTime calculationDate) {
        TaxCalculation taxCalculation = new TaxCalculation();
        taxCalculation.employee = employee;
        taxCalculation.year = year;
        taxCalculation.month = month;
        taxCalculation.tax = tax;
        taxCalculation.calculationDate = calculationDate;
        return taxCalculation;
    }

    public Employee getEmployee() {
        return employee;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public Money getTax() {
        return tax;
    }

    public DateTime getCalculationDate() {
        return calculationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaxCalculation)) return false;

        TaxCalculation that = (TaxCalculation) o;

        if (month != that.month) return false;
        if (year != that.year) return false;
        if (calculationDate != null ? !calculationDate.equals(that.calculationDate) : that.calculationDate != null)
            return false;
        if (employee != null ? !employee.equals(that.employee) : that.employee != null) return false;
        if (tax != null ? !tax.equals(that.tax) : that.tax != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = employee != null ? employee.hashCode() : 0;
        result = 31 * result + month;
        result = 31 * result + year;
        result = 31 * result + (tax != null ? tax.hashCode() : 0);
        result = 31 * result + (calculationDate != null ? calculationDate.hashCode() : 0);
        return result;
    }
}
