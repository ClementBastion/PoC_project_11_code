describe('Bed Allocation Flow', () => {
  it('passes', () => {
    cy.visit('http://localhost:5173/auth/login')

    cy.get('#«r0»').clear('dr-john');
    cy.get('#«r0»').type('dr-john');
    cy.get('#«r1»').clear('test123');
    cy.get('#«r1»').type('test123');
    cy.get('.MuiButtonBase-root').click();

    cy.get('body').click();
    cy.wait(1000);

    cy.get('[aria-labelledby="speciality-select-label"]').click();
    cy.get('[data-value="74"]').click();
    cy.get('#«r4»').click();
    cy.get('#«r4»').clear();
    cy.get('#«r4»').type('5');
    cy.get('#«r5»').clear();
    cy.get('#«r5»').type('5');

    cy.get('.MuiButtonBase-root').click();
    cy.get(':nth-child(1) > strong').should('be.visible');
    cy.get(':nth-child(2) > strong').should('be.visible');

  })
})
