with c as(
with base as(
select cust, prod, avg(quant), sum(quant),count(*)
from sales
group by cust, prod
order by prod
), base_cust as(
select b1.cust prime_cust, b1.prod, b1.avg, b2.cust, b2.prod other_prod, b2.sum other_cust_quant, b2.count other_cust_count
from base b1, base b2
where b1.prod = b2.prod and b1.cust != b2.cust
), base_other_cust as(
select prime_cust, prod, avg, sum(other_cust_quant) other_quant_sum, sum(other_cust_count) other_count_sum
from base_cust
group by prime_cust,prod,avg
)
select prime_cust customer, prod product, avg the_avg, (other_quant_sum/other_count_sum) other_cust_avg
from base_other_cust
), p as(
with base_p as(
    select cust, prod, avg(quant), sum(quant),count(*)
from sales
group by cust, prod
order by cust
), base_otherp as(
select b1.cust, b1.prod prime_prod, b1.avg prime_avg, b2.prod other_prod, b2.sum other_quant_sum, b2.count other_count_sum
from base_p b1, base_p b2
where b1.cust = b2.cust and b1.prod != b2.prod
), base_otherp1 as(
select cust, prime_prod, prime_avg, sum(other_quant_sum) other_quant_sum, 
    sum(other_count_sum) other_count_sum
from base_otherp
group by cust, prime_prod, prime_avg
)
select cust customer, prime_prod product, prime_avg the_avg, (other_quant_sum / other_count_sum) as other_prod_avg
from base_otherp1
)

select * from
p join c using(customer,product,the_avg)