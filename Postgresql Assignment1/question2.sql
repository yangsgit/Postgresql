with jan as(
    select cust, prod, max(quant) jan_max
    from sales
    where month = '1' and year between 2000 and 2005
    group by cust, prod
), jan_max as(
	select j.cust, j.prod, j.jan_max, s.month j_month, s.day j_day, s.year j_year
    from jan j, sales s
    where j.cust = s.cust and j.prod = s.prod and j.jan_max = s.quant
), feb as(
	select cust, prod, min(quant) feb_min
    from sales
    where month = 2
    group by cust, prod
), feb_min as(
	select f.cust, f.prod, f.feb_min, s.month f_month, s.day f_day, s.year f_year
    from feb f, sales s
    where f.cust = s.cust and f.prod = s.prod and f.feb_min = s.quant
), mar as(
	select cust, prod, min(quant) mar_min
    from sales
    where month = 3
    group by cust, prod
) ,mar_min as(
	select m.cust, m.prod, m.mar_min,  s.month m_month, s.day m_day, s.year m_year
    from mar m, sales s
    where m.cust = s.cust and m.prod = s.prod and m.mar_min = s.quant
)

select *
from jan_max natural full outer join feb_min natural full outer join mar_min


