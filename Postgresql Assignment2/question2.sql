with base_v as(
select cust,prod,month,quant,
case 
when month in(1,2,3)then 'Q1'
when month in(4,5,6) then 'Q2'
when month in(7,8,9) then 'Q3'
when month in(10,11,12) then 'Q4'
end as Quarter
from sales
), base_v2 as(
select cust,prod,quarter, avg(quant) avg_quant
from base_v
group by cust,prod,quarter
order by cust,prod,quarter
), base_quarter1 as(
select cust,prod,quarter, avg_quant
from base_v2
where quarter = 'Q1'
), base_quarter2 as(
select cust,prod,quarter, avg_quant
from base_v2
where quarter = 'Q2'
), base_quarter3 as(
select cust,prod,quarter, avg_quant
from base_v2
where quarter = 'Q3'
), base_quarter4 as(
select cust,prod,quarter, avg_quant
from base_v2
where quarter = 'Q4'
), result_quarter1 as(
	select cust, prod, b1.quarter, b2.avg_quant AFTER_AVG
    from base_quarter1 b1 left outer join base_quarter2 b2 using(cust,prod)
)
,result_quarter2 as(
select cust,prod, b2.quarter, b1.avg_quant BEFORE_AVG, b3.avg_quant AFTER_AVG 
from base_quarter1 b1 
    right outer join base_quarter2 b2 using(cust,prod)
	left outer join base_quarter3 b3 using(cust,prod)
), result_quarter3 as(
select cust, prod, b3.quarter, b2.avg_quant BEFORE_AVG, b4.avg_quant AFTER_AVG
from base_quarter2 b2 
    right outer join base_quarter3 b3 using(cust, prod) 
    left outer join base_quarter4 b4 using(cust, prod)
), result_quarter4 as(
select cust, prod, b4.quarter, b3.avg_quant before_avg
    from base_quarter3 b3 right outer join base_quarter4 b4
    using(cust, prod)
)
select cust,prod,quarter, before_avg, after_avg
from result_quarter1 full outer join 
result_quarter2 using (cust, prod, quarter, after_avg)
full outer join result_quarter3 using(cust, prod, quarter, before_avg, after_avg)
full outer join result_quarter4 using(cust,prod,quarter,before_avg)