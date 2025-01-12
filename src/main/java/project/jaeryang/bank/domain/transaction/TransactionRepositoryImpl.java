package project.jaeryang.bank.domain.transaction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface Dao {
    List<Transaction> findTransactionList(@Param("accountId") Long accountId,
                                          @Param("transactionType") String transactionType,
                                          @Param("page") Integer page);
}

@RequiredArgsConstructor
public class TransactionRepositoryImpl implements Dao {
    private final EntityManager em;

    //transactionType 에 따른 동적 쿼리
    @Override
    public List<Transaction> findTransactionList(Long accountId, String transactionType, Integer page) {
        String sql = "select t from Transaction t ";

        if (transactionType.equals("WITHDRAW")) {
            sql += "join fetch t.withdrawAccount wa ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId";
        } else if (transactionType.equals("DEPOSIT")) {
            sql += "join fetch t.depositAccount da ";
            sql += "where t.depositAccount.id = :depositAccountId";
        } else { //ALL
            sql += "left join fetch t.withdrawAccount wa ";
            sql += "left join fetch t.depositAccount da ";
            sql += "where t.withdrawAccount.id = :withdrawAccountId ";
            sql += "or ";
            sql += "t.depositAccount.id = :depositAccountId";
        }

        TypedQuery<Transaction> query = em.createQuery(sql, Transaction.class);
        if (transactionType.equals("WITHDRAW")) {
            query.setParameter("withdrawAccountId", accountId);
        } else if (transactionType.equals("DEPOSIT")) {
            query.setParameter("depositAccountId", accountId);
        } else {
            query.setParameter("withdrawAccountId", accountId);
            query.setParameter("depositAccountId", accountId);
        }
        int size = 5;
        query.setFirstResult(page * size); //page = 0, 0, 5, 10
        query.setMaxResults(size);

        return query.getResultList();
    }
}
