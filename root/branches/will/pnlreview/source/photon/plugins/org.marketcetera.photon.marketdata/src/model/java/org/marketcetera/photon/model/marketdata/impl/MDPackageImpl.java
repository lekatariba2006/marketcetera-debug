package org.marketcetera.photon.model.marketdata.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.marketcetera.photon.model.marketdata.MDFactory;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDLatestTick;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDTopOfBook;
import org.marketcetera.util.misc.ClassVersion;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
@ClassVersion("$Id$")
public class MDPackageImpl extends EPackageImpl implements MDPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mdItemEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mdLatestTickEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mdTopOfBookEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.marketcetera.photon.model.marketdata.MDPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private MDPackageImpl() {
		super(eNS_URI, MDFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static MDPackage init() {
		if (isInited) return (MDPackage) EPackage.Registry.INSTANCE.getEPackage(MDPackage.eNS_URI);

		// Obtain or create and register package
		MDPackageImpl theMDPackage = (MDPackageImpl) (EPackage.Registry.INSTANCE
				.getEPackage(eNS_URI) instanceof MDPackageImpl ? EPackage.Registry.INSTANCE
				.getEPackage(eNS_URI) : new MDPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theMDPackage.createPackageContents();

		// Initialize created meta-data
		theMDPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theMDPackage.freeze();

		return theMDPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMDItem() {
		return mdItemEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMDItem_Symbol() {
		return (EAttribute) mdItemEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMDLatestTick() {
		return mdLatestTickEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMDLatestTick_Price() {
		return (EAttribute) mdLatestTickEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMDLatestTick_Size() {
		return (EAttribute) mdLatestTickEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getMDTopOfBook() {
		return mdTopOfBookEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMDTopOfBook_BidSize() {
		return (EAttribute) mdTopOfBookEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMDTopOfBook_BidPrice() {
		return (EAttribute) mdTopOfBookEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMDTopOfBook_AskSize() {
		return (EAttribute) mdTopOfBookEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getMDTopOfBook_AskPrice() {
		return (EAttribute) mdTopOfBookEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MDFactory getMDFactory() {
		return (MDFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		mdItemEClass = createEClass(MD_ITEM);
		createEAttribute(mdItemEClass, MD_ITEM__SYMBOL);

		mdLatestTickEClass = createEClass(MD_LATEST_TICK);
		createEAttribute(mdLatestTickEClass, MD_LATEST_TICK__PRICE);
		createEAttribute(mdLatestTickEClass, MD_LATEST_TICK__SIZE);

		mdTopOfBookEClass = createEClass(MD_TOP_OF_BOOK);
		createEAttribute(mdTopOfBookEClass, MD_TOP_OF_BOOK__BID_SIZE);
		createEAttribute(mdTopOfBookEClass, MD_TOP_OF_BOOK__BID_PRICE);
		createEAttribute(mdTopOfBookEClass, MD_TOP_OF_BOOK__ASK_SIZE);
		createEAttribute(mdTopOfBookEClass, MD_TOP_OF_BOOK__ASK_PRICE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		mdLatestTickEClass.getESuperTypes().add(this.getMDItem());
		mdTopOfBookEClass.getESuperTypes().add(this.getMDItem());

		// Initialize classes and features; add operations and parameters
		initEClass(mdItemEClass, MDItem.class,
				"MDItem", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getMDItem_Symbol(),
				ecorePackage.getEString(),
				"symbol", null, 1, 1, MDItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(mdLatestTickEClass, MDLatestTick.class,
				"MDLatestTick", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getMDLatestTick_Price(),
				ecorePackage.getEBigDecimal(),
				"price", null, 0, 1, MDLatestTick.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getMDLatestTick_Size(),
				ecorePackage.getEBigDecimal(),
				"size", null, 0, 1, MDLatestTick.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(mdTopOfBookEClass, MDTopOfBook.class,
				"MDTopOfBook", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(
				getMDTopOfBook_BidSize(),
				ecorePackage.getEBigDecimal(),
				"bidSize", null, 0, 1, MDTopOfBook.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getMDTopOfBook_BidPrice(),
				ecorePackage.getEBigDecimal(),
				"bidPrice", null, 0, 1, MDTopOfBook.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getMDTopOfBook_AskSize(),
				ecorePackage.getEBigDecimal(),
				"askSize", null, 0, 1, MDTopOfBook.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(
				getMDTopOfBook_AskPrice(),
				ecorePackage.getEBigDecimal(),
				"askPrice", null, 0, 1, MDTopOfBook.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Create resource
		createResource(eNS_URI);
	}

} //MDPackageImpl
