with base_v as
(
    select cust, max(quant) max_q, min(quant) min_q, avg(quant) avg_q
    from sales
    group by cust
), max_v as
(
	select b.cust, b.max_q, s.prod, s.month, s.day, s.year,s.state, b.min_q, b.avg_q
    from base_v b, sales s
    where b.cust = s.cust and b.max_q = s.quant
)
select m.cust, m.max_q, m.prod product, m.month, m.day, m.year, m.state, m.min_q, s.prod product, s.month, s.day, s.year, s.state, m.avg_q
from max_v m, sales s
where m.min_q = s.quant and m.cust = s.cust