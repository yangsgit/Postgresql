with base as(
select cust,prod,
case 
when month in (1,2,3) then 'Q1'
when month in(4,5,6) then 'Q2'
when month in(7,8,9) then 'Q3'
when month in(10,11,12) then 'Q4'
end as quarter, quant
from sales
group by cust,prod,quarter,quant
), base_v as(
select cust,prod,quarter,avg(quant) avg_q,min(quant) min_q
    from base
    group by cust,prod,quarter
),
base_q1 as(
select cust,prod,quarter,avg_q,min_q
from base_v
where quarter = 'Q1'
), base_q2 as(
select cust,prod,quarter,avg_q,min_q
from base_v
where quarter = 'Q2'
), base_q3 as(
select cust,prod,quarter,avg_q,min_q
from base_v
where quarter = 'Q3'
), base_q4 as(
select cust,prod,quarter,avg_q,min_q
from base_v
where quarter = 'Q4'
),result1 as(
    with result as(
select cust ,prod, base_q1.quarter ,count(*) after_tot
from base_q1
left outer join (select cust,prod,quant from base where quarter = 'Q2') as q2
using( cust,prod)
where quant > min_q and quant < avg_q
group by cust, prod, base_q1.quarter)

select cust customer,prod product,quarter, after_tot
from result
), result2 as(
   with  before as(
select cust,prod,base_q2.quarter, count(*) before_tot
from base_q2
join(select cust,prod,quant from base where quarter = 'Q1') as Q1 using(cust,prod)
where Q1.quant >min_q and Q1.quant < avg_q
group by cust,prod,base_q2.quarter
), after as(
select cust,prod,base_q2.quarter,count(*) after_tot
from base_q2
join(select cust,prod,quarter,quant from base where quarter = 'Q3') as Q3 using(cust,prod)
where Q3.quant > min_q and Q3.quant < avg_q
group by cust,prod,base_q2.quarter)

select cust customer,prod product,quarter,before_tot,after_tot
from before full outer join after using(cust,prod,quarter)
), result3 as(
    with before as(
select cust,prod,base_q3.quarter, count(*) before_tot
from base_q3
join(select cust,prod,quant from base where quarter = 'Q2') as Q2 using(cust,prod)
where Q2.quant >min_q and Q2.quant < avg_q
group by cust,prod,base_q3.quarter
), after as(
select cust,prod,base_q3.quarter,count(*) after_tot
from base_q3
join(select cust,prod,quant from base where quarter = 'Q4') as Q4 using(cust,prod)
where Q4.quant > min_q and Q4.quant < avg_q
group by cust,prod,base_q3.quarter)

select cust customer,prod product,quarter,before_tot,after_tot
from before full outer join after using(cust,prod,quarter)
),result4 as(
    with result as(
select cust ,prod, base_q4.quarter ,count(*) before_tot
from base_q4
left outer join (select cust,prod,quant from base where quarter = 'Q3') as q3
using( cust,prod)
where quant > min_q and quant < avg_q
group by cust, prod, base_q4.quarter)

select cust customer,prod product,quarter,before_tot
from result
)
select *from result1 
full outer join result2 using(customer,product,quarter,after_tot)
full outer join result3 using(customer,product,quarter,before_tot,after_tot)
full outer join result4 using(customer,product,quarter,before_tot)






