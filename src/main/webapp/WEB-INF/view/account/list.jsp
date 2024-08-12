<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- header.jsp  -->
<%@ include file="/WEB-INF/view/layout/header.jsp"%>

<!-- start of content.jsp(xxx.jsp)   -->
<div class="col-sm-8">
	<h2>계좌 생성(인증)</h2>
	<h5>Bank App에 오신걸 환영합니다</h5>
	<!-- insert into account_tb(number, password, balance, created_at, user_id) -->

	<c:choose>
		<c:when test="${accountList != null}">
			<%-- 계좌(accountList)가 존재하는 경우. 참고! JSP 내에서 HTML 주석 사용을 한다면 Java 파일 컴파일 시 오류 발생 가능성 있음 --%>
			<table class="table">
				<thead>
					<tr>
						<th>계좌 번호</th>
						<th>잔액</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach var="account" items="${accountList}">
						<tr>
							<td><a href="/account/detail/${account.id}?type=all">${account.number}</a></td>
							<td>${account.formatKoreanWon(account.balance)}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>

		<c:otherwise>
			<div class="jumbotron display-4">
				<h5>아직 생성된 계좌가 없습니다.</h5>
			</div>
		</c:otherwise>
	</c:choose>

</div>
<!-- end of col-sm-8  -->
</div>
</div>
<!-- end of content.jsp(xxx.jsp)   -->

<!-- footer.jsp  -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>