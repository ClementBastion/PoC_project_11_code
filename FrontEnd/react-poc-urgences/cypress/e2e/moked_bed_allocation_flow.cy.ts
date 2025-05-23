describe('Bed Allocation Flow', () => {

  beforeEach(() => {
    cy.intercept('POST', '**/protocol/openid-connect/token', {
      statusCode: 200,
      body: {
        access_token: 'fake-access-token',
        refresh_token: 'fake-refresh-token',
        expires_in: 3600,
        token_type: 'bearer'
      }
    });
    cy.intercept('GET', '**/api/specialities', {
      statusCode: 200,
      body: [
        { id: 1, name: 'Cardiology' },
        { id: 2, name: 'Immunology' }
      ]
    });
    cy.intercept('GET', '**/api/hospitals/search?lat=50&lon=50&specialityId=2', {
      statusCode: 200,
      body:
        { name: "Moked hospital", latitude: 51, longitude: 49 },
    });
  });

  it('passes', () => {
    cy.visit('http://localhost:5173/auth/login')

    cy.get('#«r0»').clear('dr-john');
    cy.get('#«r0»').type('dr-john');
    cy.get('#«r1»').clear('test123');
    cy.get('#«r1»').type('test123');
    cy.get('.MuiButtonBase-root').click();

    cy.get('body').click();
    cy.wait(1000);

    cy.get('[aria-labelledby="speciality-select-label"]').click({ multiple: true, force: true  });
    cy.get('[data-value="2"]').click();
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
